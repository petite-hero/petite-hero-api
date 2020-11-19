package capstone.petitehero.services;

import capstone.petitehero.config.common.Constants;
import capstone.petitehero.dtos.common.ConfigChangeDTO;
import capstone.petitehero.dtos.common.LicenseDTO;
import capstone.petitehero.dtos.request.location.PushNotiSWDTO;
import capstone.petitehero.entities.Child;
import capstone.petitehero.repositories.ChildRepository;
import capstone.petitehero.utilities.XMLUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.List;

@Service
public class ConfigService {

    @Autowired
    private ChildRepository childRepository;

    @Autowired
    private NotificationService notiService;

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
            // check field change
            Boolean changeOuterRadius = Boolean.FALSE, changeReportDelay = Boolean.FALSE;
            LicenseDTO licenseDTO = getLicenseForAdmin();
            if (licenseDTO != null) {
                if (licenseDTO.getOuter_radius().intValue() == modifyLicenseDTO.getOuter_radius().intValue()) {
                    changeOuterRadius = Boolean.TRUE;
                }
                if (licenseDTO.getReport_delay().intValue() == modifyLicenseDTO.getReport_delay().intValue()) {
                    changeReportDelay = Boolean.TRUE;
                }
            }

            File f = ResourceUtils.getFile(Constants.CONFIG_FILE);

            String xmlString = XMLUtil.marshal(modifyLicenseDTO);

            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(f));
            bufferedWriter.write(xmlString);
            bufferedWriter.close();

            // send noti for smart watch
            if (changeOuterRadius || changeReportDelay) {
                List<Child> childList = childRepository.findChildrenByIsDisabled(Boolean.FALSE);
                ConfigChangeDTO configChangeDTO = new ConfigChangeDTO();
                if (changeOuterRadius) {
                    configChangeDTO.setOuter_radius(modifyLicenseDTO.getOuter_radius());
                }
                if (changeReportDelay) {
                    configChangeDTO.setReport_delay(modifyLicenseDTO.getReport_delay());
                }

                PushNotiSWDTO noti = new PushNotiSWDTO(Constants.SILENT_NOTI, Constants.UPDATED_CONFIG, configChangeDTO);
                if (childList != null && !childList.isEmpty()) {
                    String pushToken;

                    for (Child child : childList) {
                        if (child.getPushToken() != null && !child.getPushToken().isEmpty()) {
                            pushToken = child.getPushToken();
                            notiService.pushNotificationSW(noti, pushToken);
                        }
                    }
                }
            }

            return Constants.status.UPDATED.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
