#!/bin/bash
# usage : ./git_archive.sh <repo_identifier> <old_commit_id> <new_commit_id> <commit_number>

if [ $# -lt 4 ]; then
    echo "./git_archive.sh <repo_identifier> <old_commit_id> <new_commit_id> <commit_number>"
    exit
fi

cd repos/$1
#if [ ! -e "diffs/$4/$2-$3.diff" ]; then
if [ ! -e diffs/$4 ]; then
    mkdir diffs/$4
fi
if [ ! -e diffs/$4/$2-$3.diff ]; then
    git diff $2 $3 > diffs/$4/$2-$3.diff
fi
#fi
