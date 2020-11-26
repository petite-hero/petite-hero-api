package capstone.petitehero.services;

import capstone.petitehero.config.common.Constants;
import capstone.petitehero.dtos.ResponseObject;
import capstone.petitehero.dtos.request.location.AddNewSafeZoneRequestDTO;
import capstone.petitehero.dtos.request.location.PushNotiSWDTO;
import capstone.petitehero.dtos.request.location.UpdateSafeZoneRequestDTO;
import capstone.petitehero.dtos.response.location.GetListSafeZoneByDateResponseDTO;
import capstone.petitehero.dtos.response.location.GetSafeZoneDetailResponseDTO;
import capstone.petitehero.dtos.response.location.SafeZoneChangedResponseDTO;
import capstone.petitehero.entities.Child;
import capstone.petitehero.entities.Parent;
import capstone.petitehero.entities.Safezone;
import capstone.petitehero.repositories.ChildRepository;
import capstone.petitehero.repositories.ParentRepository;
import capstone.petitehero.repositories.SafeZoneRepository;
import capstone.petitehero.utilities.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SafeZoneService {

    @Autowired
    private SafeZoneRepository safeZoneRepository;

    @Autowired
    private ChildRepository childRepository;

    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private NotificationService notiService;

    public ResponseObject addSafeZone(AddNewSafeZoneRequestDTO sentSafeZone) {
        ResponseObject result = Util.createResponse();
        try {
            Child child = childRepository.getOne(sentSafeZone.getChildId());
            Parent parent = parentRepository.findParentByAccount_Username(sentSafeZone.getCreator());

            if (child == null || parent == null) {
                result.setData(null);
                result.setMsg("Bad request - Child or Parent doesn't exist");
                result.setCode(Constants.CODE_400);
            } else {
                Safezone addedSafeZone = new Safezone();
                addedSafeZone.setName(sentSafeZone.getName());
                addedSafeZone.setLatitude(sentSafeZone.getLatitude());
                addedSafeZone.setLongitude(sentSafeZone.getLongitude());
                addedSafeZone.setFromTime(sentSafeZone.getFromTime());
                addedSafeZone.setToTime(sentSafeZone.getToTime());
                addedSafeZone.setDate(sentSafeZone.getDate());

                addedSafeZone.setLatA(sentSafeZone.getLatA());
                addedSafeZone.setLngA(sentSafeZone.getLngA());
                addedSafeZone.setLatB(sentSafeZone.getLatB());
                addedSafeZone.setLngB(sentSafeZone.getLngB());
                addedSafeZone.setLatC(sentSafeZone.getLatC());
                addedSafeZone.setLngC(sentSafeZone.getLngC());
                addedSafeZone.setLatD(sentSafeZone.getLatD());
                addedSafeZone.setLngD(sentSafeZone.getLngD());
                if (sentSafeZone.getRepeatOn() == null || sentSafeZone.getRepeatOn().isEmpty()) {
                    addedSafeZone.setRepeatOn(null);
                } else {
                    addedSafeZone.setRepeatOn(sentSafeZone.getRepeatOn());
                }
                addedSafeZone.setRadius(sentSafeZone.getRadius());
                addedSafeZone.setIsDisabled(false);
                addedSafeZone.setType(sentSafeZone.getType());
                addedSafeZone.setChild(child);
                addedSafeZone.setParent(parent);

                Safezone safezone = safeZoneRepository.save(addedSafeZone);

                if (safezone != null) {
                    result.setData(sentSafeZone);
                    result.setMsg("Added successfully!");

                    //  notify SW if current date' safe zones changed
                    SafeZoneChangedResponseDTO data = Util.convertSafeZoneToReponseObj(safezone, Constants.ADDED);
                    Integer pushStatus = notiService.notifySWSafeZoneChanges(data, safezone.getChild().getPushToken(), null);
                    if (pushStatus == Constants.CODE_200) {
                        result.setMsg(Constants.NO_ERROR);
                    } else {
                        result.setMsg("Added safe zone but error occurred when notifying SW");
                    }

                } else {
                    result.setData(null);
                    result.setMsg("Error occurred while adding safe zone");
                    result.setCode(Constants.CODE_500);
                }
            }
        } catch (Exception e) {
            result.setData(null);
            result.setMsg(Constants.SERVER_ERROR + e.toString());
            result.setCode(Constants.CODE_500);
        }
        return  result;
    }

    public ResponseObject getListSafeZoneByDate(Long childId, Long currentDate) {
        ResponseObject result = Util.createResponse();
        try {
            Child child = childRepository.getOne(childId);
            if (child == null) {
                result.setData(null);
                result.setMsg("Bad request - Child doesn't exist");
                result.setCode(Constants.CODE_400);
            } else {
                String weekdayREGEX = Util.getWeekdayRegex(currentDate);
                List<Safezone> rawData = safeZoneRepository.getListSafeZone(childId, currentDate, weekdayREGEX);
                List<GetListSafeZoneByDateResponseDTO> filteredData = Util.castToSafeZoneResponse(rawData);
                result.setData(filteredData);
                result.setMsg(Constants.NO_ERROR);
            }
        } catch (Exception e) {
            result.setData(null);
            result.setMsg(Constants.SERVER_ERROR + e.toString());
            result.setCode(Constants.CODE_500);
        }
        return result;
    }

    public ResponseObject getSafeZoneDetail(Long safezoneId) {
        ResponseObject result = Util.createResponse();
        try {
            Safezone safezone = safeZoneRepository.getOne(safezoneId);
            if (safezone == null) {
                result.setData(null);
                result.setMsg("Bad request - SafeZone doesn't exist");
                result.setCode(Constants.CODE_400);
            } else {
                GetSafeZoneDetailResponseDTO response = new GetSafeZoneDetailResponseDTO(
                        safezone.getSafezoneId(),
                        safezone.getName(),
                        safezone.getLatitude(),
                        safezone.getLongitude(),
                        safezone.getFromTime(),
                        safezone.getToTime(),
                        safezone.getDate(),
                        safezone.getRepeatOn(),
                        safezone.getRadius(),
                        safezone.getIsDisabled(),
                        safezone.getType(),
                        safezone.getChild().getChildId(),
                        safezone.getParent().getId(),
                        safezone.getLatA(),
                        safezone.getLngA(),
                        safezone.getLatB(),
                        safezone.getLngB(),
                        safezone.getLatC(),
                        safezone.getLngC(),
                        safezone.getLatD(),
                        safezone.getLngD()
                );
                result.setData(response);
                result.setMsg(Constants.NO_ERROR);
            }
        } catch (Exception e) {
            result.setData(null);
            result.setMsg(Constants.SERVER_ERROR + e.toString());
            result.setCode(Constants.CODE_500);
        }
        return result;
    }

    public ResponseObject deleteSafeZone (Long safezoneId) {
        ResponseObject result = Util.createResponse();
        try {
            Safezone safezone = safeZoneRepository.getOne(safezoneId);
            if (safezone == null) {
                result.setData(null);
                result.setMsg("Bad request - Safe Zone doesn't exist");
                result.setCode(Constants.CODE_400);
            } else {
                safezone.setIsDisabled(Constants.IS_DISABLED);
                Safezone updatedSafezone = safeZoneRepository.save(safezone);

                if (updatedSafezone != null) {
                    //  notify SW if current date' safe zones changed
                    System.out.println("Before: " + updatedSafezone.getRepeatOn());
                    SafeZoneChangedResponseDTO data = Util.convertSafeZoneToReponseObj(updatedSafezone, Constants.DELETED);
                    System.out.println("After: " + data.getRepeatOn());
                    Integer pushStatus = notiService.notifySWSafeZoneChanges(data, safezone.getChild().getPushToken(), updatedSafezone.getDate());
                    if (pushStatus == Constants.CODE_200) {
                        result.setMsg(Constants.NO_ERROR);
                    } else {
                        result.setMsg("Deleted safe zone but error occurred when notifying SW");
                    }
                    result.setData(null);
                }
            }
        } catch (Exception e) {
            result.setData(null);
            result.setMsg(Constants.SERVER_ERROR + e.toString());
            result.setCode(Constants.CODE_500);
        }
        return result;
    }

    public ResponseObject updateSafeZone (UpdateSafeZoneRequestDTO dto) {
        ResponseObject result = Util.createResponse();
        try {
            Safezone safezone = safeZoneRepository.getOne(dto.getSafezoneId());
            if (safezone == null) {
                result.setData(null);
                result.setMsg("Bad request - Safe Zone doesn't exist");
                result.setCode(Constants.CODE_400);
            } else {
                Long beforeUpdateDate = safezone.getDate();
                if (dto.getName() != null && !dto.getName().isEmpty()) {
                    safezone.setName(dto.getName());
                }
                if (dto.getLatitude() != null) {
                    safezone.setLatitude(dto.getLatitude());
                }
                if (dto.getLongitude() != null) {
                    safezone.setLongitude(dto.getLongitude());
                }
                if (dto.getFromTime() != null) {
                    safezone.setFromTime(dto.getFromTime());
                }
                if (dto.getToTime() != null) {
                    safezone.setToTime(dto.getToTime());
                }
                if (dto.getDate() != null) {
                    safezone.setDate(dto.getDate());
                }
                if (dto.getRepeatOn() != null && !dto.getRepeatOn().isEmpty()) {
                    safezone.setRepeatOn(dto.getRepeatOn());
                }
                if (dto.getRadius() != null) {
                    safezone.setRadius(dto.getRadius());
                }
                if (dto.getType() != null && !dto.getType().isEmpty()) {
                    safezone.setType(dto.getType());
                }

                if (dto.getLatA() != null) {
                    safezone.setLatA(dto.getLatA());
                }
                if (dto.getLngA() != null) {
                    safezone.setLngA(dto.getLngA());
                }
                if (dto.getLatB() != null) {
                    safezone.setLatB(dto.getLatB());
                }
                if (dto.getLngB() != null) {
                    safezone.setLngB(dto.getLngB());
                }
                if (dto.getLatC() != null) {
                    safezone.setLatC(dto.getLatC());
                }
                if (dto.getLngC() != null) {
                    safezone.setLngC(dto.getLngC());
                }
                if (dto.getLatD() != null) {
                    safezone.setLatD(dto.getLatD());
                }
                if (dto.getLngD() != null) {
                    safezone.setLngD(dto.getLngD());
                }

                Safezone updatedSafezone = safeZoneRepository.save(safezone);
                if (updatedSafezone != null) {
                    //  notify SW if current date' safe zones changed

                    SafeZoneChangedResponseDTO data = Util.convertSafeZoneToReponseObj(updatedSafezone, Constants.UPDATED);
                    Integer pushStatus = notiService.notifySWSafeZoneChanges(data, safezone.getChild().getPushToken(), beforeUpdateDate);

                    if (pushStatus == Constants.CODE_200) {
                        result.setMsg(Constants.NO_ERROR);
                    } else {
                        result.setMsg("Updated safe zone but error occurred when notifying SW");
                    }
                    result.setData(null);
                }
            }

        } catch (Exception e) {
            result.setData(null);
            result.setMsg(Constants.SERVER_ERROR + e.toString());
            result.setCode(Constants.CODE_500);
            e.printStackTrace();
        }
        return result;
    }
}
