# CRISPR-X V.1.0.x

## structure

Android/ 			The Android project of CRISPR-X
desktop/			The desktop version of CRISPR-X
server/				The back-end of CRISPR-X, mainly calculation core written in C/C++
.travis.yml         Provide [Travis CI](https://travis-ci.org/) automatically build and [Coveralls](https://coveralls.io/) auto-test.

## server
[![Build Status](https://travis-ci.org/uestc-igem-2014/CRISPR-X.svg?branch=master)](https://travis-ci.org/uestc-igem-2014/CRISPR-X)
[![Coverage Status](https://coveralls.io/repos/uestc-igem-2014/CRISPR-X/badge.png)](https://coveralls.io/r/uestc-igem-2014/CRISPR-X)

Find potential CRISPR/Cas9 sgRNAs and give a genome-wide evaluation.

## Android

On Android, provide a form to submit model organism, RFC, target gene. Communicate with the server through JSON to get the result.

Manually test passed.

# Desktop

Java program to support multiple desktop platforms. Provide the same functions as Android version.

Manually test passed.

