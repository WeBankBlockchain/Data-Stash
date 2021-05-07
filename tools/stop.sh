#!/bin/bash
ps -ef|grep Data-Stash |grep -v grep| awk '{print $2}'|xargs kill -9