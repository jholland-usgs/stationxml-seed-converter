package edu.iris.dmc.station.converter;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.Test;

import edu.iris.dmc.IrisUtil;
import edu.iris.dmc.fdsn.station.model.FDSNStationXML;
import edu.iris.dmc.seed.Blockette;
import edu.iris.dmc.seed.Volume;

public class DocumentConverterTest {

	@Test
	public void t1() {
		File source = null, target = null;
		try {

			source = new File(DocumentConverterTest.class.getClassLoader()
					.getResource("IM_DATALESS_I58_infrasound_BDF_20170524.dataless").getFile());

			final Volume original = IrisUtil.readSeed(source);
			FDSNStationXML document = SeedToXmlDocumentConverter.getInstance().convert(original);

			Volume converted = XmlToSeedDocumentConverter.getInstance().convert(document);

			List<Blockette> oList = original.getAll();
			List<Blockette> cList = converted.getAll();
			
			//assertEquals(oList.size(),cList.size());
			
			assertEquals(converted.getB050s().size(),original.getB050s().size());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (source != null) {
			}

			if (target != null) {

			}
		}

	}

}
