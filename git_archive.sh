#!/bin/bash
# usage : ./git_archive.sh <repo_identifier> <old_commit_id> <new_commit_id> <commit_number>

if [ $# -lt 4 ]; then
    echo "./git_archive.sh <repo_identifier> <old_commit_id> <new_commit_id> <commit_number>"
    exit
fi

cd repos/$1
java_files=`git diff --name-only $2 $3 | grep "\.java"`
if [ -n "$java_files"  -a ! -e diffs/$4 ]; then
    git archive --format=zip --prefix=diffs/$4/older/ $2 $java_files -o archive_older.zip
    unzip archive_older.zip > /dev/null
    #rm -f archive_older.zip
    git archive --format=zip --prefix=diffs/$4/newer/ $3 $java_files -o archive_newer.zip
    unzip archive_newer.zip > /dev/null
    #rm -f archive_newer.zip
fi
