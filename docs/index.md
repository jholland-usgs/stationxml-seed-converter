---
title: Home
layout: default
---


# User's Guide the IRIS seed-xml-converter 

## Description

The seed-xml-converter is a tool used to converter seismological metadata between dataless seed and stationxml file formats. The converter is designed to transform the metadata with minimum losses. Documentation describing the inherent differences between Dataless and Stationxml metadata can be found at [Losses between Dataless and StationXML](https://github.com/iris-edu/stationxml-seed-converter/wiki/Information-Lost-by-Converting-from-Dataless-to-StationXML). 

## Usage

`--input` and `--output` are the only arguments supplied to the seed-xml-converter. The argument `--input` can be either dataless or stationxml files or a directory containing only dataless or xml. The `--output` and `--input` arguments must have matching path levels (file/directory). If a directory is provided, the converter will output converted files with the naming convention input_file_name.converted.dataless or input_file_name.converted.xml to the output path supplied by the user.

  `java -jar PATH/TO/seed-xml-converter-0.0.2.jar --input /PATH/TO/XML_file.xml --output /PATH/TO/XML_file.dataless`

  `java -jar /PATH/TO/seed-xml-converter-0.0.2.jar --input /PATH/TO/Dataless_file.dataless --output /PATH/TO/Dataless_file.xml`

  `java -jar /PATH/TO/seed-xml-converter-0.0.2.jar --input /PATH/TO/XML_directory --output /PATH/TO/XD_Directory/xml.converted.dataless`

  `java -jar /PATH/TO/seed-xml-converter-0.0.2.jar --input /PATH/TO/Dataless_directory --output /PATH/TO/DX_Directory/dataless.converted.xml`
  

### Errror

The converter throws exception errors if the byte length of stationXML values exceeds the dataless predefined format or if a dataless files are mis-formatted or corrupt. Refer to the [SEED manual](https://www.fdsn.org/seed_manual/SEEDManual_V2.4.pdf) for further help and documetation. 



