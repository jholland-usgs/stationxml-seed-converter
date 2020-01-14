package edu.iris.dmc.station.converter;



import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.Test;

import edu.iris.dmc.IrisUtil;
import edu.iris.dmc.fdsn.station.model.FDSNStationXML;
import edu.iris.dmc.seed.Blockette;
import edu.iris.dmc.seed.Volume;

public class Blockettes40sToXmlTest {

	@Test
	public void t2() {
		File source = null, target = null;

		source = new File(
				XmlToSeedDocumentConverterTest.class.getClassLoader().getResource("AI.dataless_arc.BELA.fromORFEUS").getFile());

		Volume volume;
		try {
			volume = IrisUtil.readSeed(source);

			FDSNStationXML document = SeedToXmlDocumentConverter.getInstance().convert(volume);
			Volume other = XmlToSeedDocumentConverter.getInstance().convert(document);

			List<Blockette> v = volume.getControlBlockettes();
			
			for(Blockette b:v) {
				System.out.println(b.toSeedString());
			}
			
			System.out.println("-----------------------------");
			List<Blockette> o = other.getControlBlockettes();
			for(Blockette b:o) {
				System.out.println(b.toSeedString());
			}

			//assertEquals(volume.getControlBlockettes().size(), other.getControlBlockettes().size());
			//assertEquals(volume.getIndexBlockettes().size(), other.getIndexBlockettes().size());
			//assertEquals(volume.getDictionaryBlockettes().size(), other.getDictionaryBlockettes().size());
			//assertEquals(volume.size(), other.size());

			//compare(volume, other);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void compare(Volume original, Volume other) throws Exception {

		assertEquals(original.size(), other.size());

	}

}
