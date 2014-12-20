#!/bin/bash

echo "Are you sure to delete all diff files? [Y/n]"
read ans
if [ "$ans" != "Y" ]; then
    echo "Aborted."
    exit
fi

rm -rf repos/*/*/diffs
echo "All diffs deleted."
