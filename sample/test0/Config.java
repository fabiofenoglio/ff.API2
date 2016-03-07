package test0;

import java.io.FileReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
public class Config {
	public static final String ENCODING = "UTF-8";
	
	/*
	 * Actual configuration fields
	 */
	public String username = "";
	public String password = "";
	public Integer runCounter = 0;
	
	/*
	 * The following fields will not be serialized (@XmlTransient)
	 */
	@XmlTransient
	public String filePath = "";
	
	@XmlTransient
	public boolean isNew = true;
	
	public Config() {
		// no initialization required
	}

	public Config(String filePath) {
		this.filePath = filePath;
	}
	
	/*
	 * Ensure JAXBContext is coherent in serialization and deserialization
	 */
	protected static JAXBContext getJAXBContext() throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(Config.class);
		return jaxbContext;
	}
	
	/*
	 * Shortcut to create new instance if the required one is missing
	 */
	public static Config loadOrNew(String filePath) throws Exception {
		try {
			return load(filePath);
		}
		catch (Exception e) {
			return new Config(filePath);
		}
	}
	
	/*
	 * Load configuration from file
	 * [STATIC]
	 */
	public static Config load(String filePath) throws Exception {
		JAXBContext jaxbContext = getJAXBContext();
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

		FileReader reader = new FileReader(filePath);
		Config cfg = (Config) unmarshaller.unmarshal(reader);
		reader.close();
		
		cfg.filePath = filePath;
		cfg.isNew = false;
		
		return cfg;
	}
	
	/*
	 * Save configuration to file
	 */
	public boolean save() throws Exception {
		if (this.filePath == null || this.filePath == "") {
			throw new Exception("FilePath is not specified");
		}
		
		String xmlString = "";
	    
		JAXBContext jaxbContext = getJAXBContext();
        Marshaller m = jaxbContext.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        
        StringWriter sw = new StringWriter();
        m.marshal(this, sw);
        xmlString = sw.toString();

        PrintWriter writer = new PrintWriter(this.filePath, Config.ENCODING);
        writer.print(xmlString);
        writer.close();
        
        return true;
	}
}
