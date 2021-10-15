#!/bin/bash
echo "user-script start"
timedatectl set-timezone Asia/Tokyo
localectl set-locale LANG=ja_JP.UTF-8
echo "export LC_ALL=ja_JP.UTF-8" >> /etc/profile
yum update -y
echo "user-script end"
