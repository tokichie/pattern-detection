#!/bin/sh
# usage : ./git_archive.sh <repo_identifier> <old_commit_id> <new_commit_id>

cd repos/$1
java_files=`git diff --name-only $2 $3 | grep "\.java"`
if [ -n "$java_files" ]; then
    git archive --format=zip --prefix=diffs/$2-$3/ $2 $java_files -o archive.zip
    unzip archive.zip > /dev/null
    rm -f archive.zip
fi
