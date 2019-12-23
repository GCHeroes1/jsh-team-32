#!/bin/bash
docker build -t jsh-test .
python3.7 tests.py -v 2>&1 | tee test_output
echo "Passed" "$(grep -c "... ok" < test_output)" "tests"
echo "Failed" "$(grep -c "... FAIL" < test_output)" "tests"
