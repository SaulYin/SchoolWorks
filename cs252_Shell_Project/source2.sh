#!/usr/local/bin/bash
while IFS= read -r cmd;do
        $cmd
done < "$1"
~              
