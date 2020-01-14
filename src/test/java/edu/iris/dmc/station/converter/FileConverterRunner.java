package edu.iris.dmc.station.converter;

import java.io.File;

import org.junit.jupiter.api.Test;

import edu.iris.dmc.seed.Volume;

public class FileConverterRunner {

	@Test
	public static void main(String[] args) {
		File source = null, target = null;

		// source = new
		// File("/Users/Suleiman/seed/AK_CCB.dataless");//dataless-archive/IU.dataless");
		source = new File("/Users/tronan/Desktop/Yazan_Dataless/ROIPH5.xml");

		Volume volume;
		try {
			((XmlToSeedFileConverter) XmlToSeedFileConverter.getInstance()).convert(source, new File("ROIPH5.xml"),
					null);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
