package com.webank.blockchain.data.stash.read;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import com.webank.blockchain.data.stash.constants.BinlogConstants;
import com.webank.blockchain.data.stash.entity.RemoteServerInfo;
import com.webank.blockchain.data.stash.fetch.BinlogLocationBO;
import com.webank.blockchain.data.stash.utils.BinlogFileUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.*;

@Slf4j
public class MultiSourceBlockReader implements Closeable {

    private String binlogSuffix;
    private List<RemoteServerInfo> sources;
    private Map<RemoteServerInfo, TreeSet<Long>> binlogFiles;
    private Map<RemoteServerInfo, Long> currentBinlogFiles;
    private Map<RemoteServerInfo, InputStream> inputStreams;

    private byte[] lengthBytes = new byte[BinlogConstants.BLOCK_LENGTH];

    public MultiSourceBlockReader(List<RemoteServerInfo> sources, long initBlockNumber, String binlogSuffix) throws IOException{
        this.sources = sources;
        this.binlogSuffix = binlogSuffix;
        init(initBlockNumber);
    }

    public MultiSourceBlockReader(List<RemoteServerInfo> sources, long initBlockNumber) throws IOException{
        this(sources, initBlockNumber, "binlog");
    }

    public List<byte[]> read() throws IOException{
        /**
         * 1. Locate each local file
         */
        List<byte[]> result = new ArrayList<>(this.sources.size());
        for(RemoteServerInfo source:this.sources){
            InputStream in = this.inputStreams.get(source);
            byte[] blockData = readBlock(in);
            //Current file EOF.
            if(blockData == null){
                //This block is not generated yet, wait.
                if(noreMoreFileToRead(source)){
                    log.info("No more file to read");
                    return null;
                }
                //Scroll to next binlog
                else{
                    log.info("Scroll to next binlog");
                    in = scrollToNextFile(source);
                    log.info("Next binlog is {}.{}",currentBinlogFiles.get(source),binlogSuffix);
                    blockData = readBlock(in);
                }
            }
            result.add(blockData);
        }
        return result;
    }

    private boolean noreMoreFileToRead(RemoteServerInfo source) {
        Long currentBinlog = this.currentBinlogFiles.get(source);
        TreeSet<Long> filesSet = BinlogFileUtils.getFileIds(source.getLocalFilePath(), binlogSuffix);
        return filesSet.higher(currentBinlog) == null;
    }


    private InputStream scrollToNextFile(RemoteServerInfo source) throws IOException {
        //Closing old
        InputStream oldStream = this.inputStreams.get(source);
        if(oldStream != null) {
            try{
                oldStream.close();
            }catch (Exception ex){}
        }
        //Prepare new stream
        long current = this.currentBinlogFiles.get(source);
        long next = this.binlogFiles.get(source).higher(current);
        InputStream newStream = prepareStreamByBlockNumber(next, source);
        return newStream;
    }

    private void init(long initBlockNumber) throws IOException{
        this.binlogFiles = new HashMap<>(sources.size());
        this.inputStreams = new HashMap<>(sources.size());
        this.currentBinlogFiles = new HashMap<>(sources.size());

        for(RemoteServerInfo source:this.sources){
            binlogFiles.put(source, BinlogFileUtils.getFileIds(source.getLocalFilePath(), binlogSuffix));
            prepareStreamByBlockNumber(initBlockNumber, source);
        }
    }

    private InputStream prepareStreamByBlockNumber(long blockNumber, RemoteServerInfo source) throws IOException{
        //1. Location file
        TreeSet<Long> filesSet = BinlogFileUtils.getFileIds(source.getLocalFilePath(), binlogSuffix);
        Long floor = filesSet.floor(blockNumber);
        if(floor == null) {
            throw new IOException("No binlog found for block "+blockNumber);
        }
        BinlogLocationBO location = this.locate(source, floor);
        //2. Location position
        InputStream newStream = FileUtil.getInputStream(location.getFilePath());
        seekToPosition(floor, newStream, blockNumber);
        //3. Update current status
        this.inputStreams.put(source, newStream);
        this.currentBinlogFiles.put(source, floor);
        return newStream;
    }


    private BinlogLocationBO locate(RemoteServerInfo serverInfo, long floor) {
        BinlogLocationBO bo = new BinlogLocationBO();
        bo.setFilePath(serverInfo.getLocalFilePath() + floor + "." +binlogSuffix);
        return bo;
    }

    private void seekToPosition(long binlogStartBlock, InputStream in, long blockNumber) throws IOException{
        if(blockNumber < binlogStartBlock){
            throw new IOException("Invalid block number "+blockNumber + " versus binlog file "+binlogStartBlock);
        }
        in.skip(4);
        //First block
        if(blockNumber == binlogStartBlock){
            log.debug("first block in binlog");
            return;
        }
        //Otherwise
        long target = blockNumber - 1;
        while(true){
            byte[] blockData = readBlock(in);
            if(blockData == null) {
                log.warn("block number {} not found in {}.binlog",blockNumber,binlogStartBlock);
                break;
            }
            long num = ByteBuffer.wrap(blockData).getLong();
            if(num == target){
                return;
            }
        }
    }

    private byte[] readBlock(InputStream in) throws IOException{
        int nread = in.read(this.lengthBytes);
        if(nread == -1) return null;
        if(nread != BinlogConstants.BLOCK_LENGTH) throw new IOException("binlog length bytes read only "+nread +" bytes");

        int blockLength = Convert.bytesToInt(this.lengthBytes);
        byte[] content = new byte[blockLength];
        nread = in.read(content);
        if(nread != blockLength) throw new IOException("binlog truncated");
        return content;
    }

    @Override
    public void close() throws IOException {
        for(InputStream in:this.inputStreams.values()){
            if(in != null){
                try{
                    in.close();
                }
                catch (Exception ex){}
            }
        }
    }
}
