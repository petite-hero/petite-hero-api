package capstone.petitehero.services;

import capstone.petitehero.config.common.Constants;
import capstone.petitehero.dtos.common.LicenseDTO;
import capstone.petitehero.utilities.XMLUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.*;

@Service
public class ConfigService {

    public LicenseDTO getLicenseForAdmin() {
        try {
            File f = ResourceUtils.getFile(Constants.CONFIG_FILE);
            InputStream in = new FileInputStream(f);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while (br.ready()) {
                line += br.readLine() + "\n";
            }
            LicenseDTO result = XMLUtil.unmarshal(line, LicenseDTO.class);

            br.close();
            in.close();

            return result;
        } catch (Exception e) {
            return null;
        }
    }

    public String modifyLicense(LicenseDTO modifyLicenseDTO) {
        try {
            File f = ResourceUtils.getFile(Constants.CONFIG_FILE);

            String xmlString = XMLUtil.marshal(modifyLicenseDTO);

            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(f));
            bufferedWriter.write(xmlString);
            bufferedWriter.close();

            return Constants.status.UPDATED.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
