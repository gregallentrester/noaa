
### NOAA Forecast - Broadcom
---
##### Dry Run
After cloning the code into this directory: \
~/stage/broadcom

A dry run can be performed by sourcing this script, in the directory
**~/stage/broadcom/**
`code`
  ok
  ---

 ##### ßuild
A ßuild can be performed in the directory \
**~/stage/broadcom/**
`code`
  bld
  ---

 ##### Execute
Executing the code with formal args can be performed in the directory \
**~/stage/broadcom/**
By supplying a set of comma-separated, quoted geocodes in this order  N,W:
`code`
  mvn spring-boot:run -q -B -Dspring-boot.run.arguments="37.3541,121.9552"
