# red-technical-test
Coding test for job application


# Questions:

- Can there be other record types appearing in the file?
- Are dates always in ascending order as they appear in the file? and is this the order the SortedMap should have them? (assumed so)
- As its not stated, can there be multiple meter reads for one NMI on the same day, and how to treat this situation if it arises? (Worked under the assumption this is not the case for now)
- How should files that do not start with RecordType 100 or end with RecordType 900 be handled?
- What is the expected behavior if the file contains malformed records (missing fields, incorrect enums or dates .etc)?
- Should it ignore, log, or stop processing if it finds invalid records?
- What error handling or logging is expected in case of invalid data or parsing errors?
- Is there an expected size or line limit for large files?

# Instructions:

You should be able to run the test harness class to output the NMI total values for 'SimpleNem12.csv'.
If you wish you can try a different SimpleNem file by adding it to the resources folder and changing the file name imported in 'TestHarness'.

You may also like to run the unit tests in 'AppTest', I included the examples from the test harness and added a few more positive and negative tests.

