#!/usr/bin/env bash

ps -ef|grep -i `pwd`|grep -i data-stash|grep -v grep| awk '{print $2}'|xargs -r kill -9
echo "Stop succeed!"

