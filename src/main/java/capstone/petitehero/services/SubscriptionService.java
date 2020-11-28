package capstone.petitehero.services;

import capstone.petitehero.config.common.Constants;
import capstone.petitehero.dtos.response.subscription.type.SubscriptionTypeStatusResponseDTO;
import capstone.petitehero.dtos.response.subscription.type.ModifySubscriptionTypeResponseDTO;
import capstone.petitehero.dtos.response.subscription.type.SubscriptionTypeDetailResponseDTO;
import capstone.petitehero.entities.Subscription;
import capstone.petitehero.entities.SubscriptionType;
import capstone.petitehero.repositories.SubscriptionRepository;
import capstone.petitehero.repositories.SubscriptionTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubscriptionService {

    @Autowired
    private SubscriptionTypeRepository subscriptionTypeRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private NotificationService notiService;

    public SubscriptionTypeStatusResponseDTO createNewSubscriptionType(SubscriptionType subscriptionType) {
        SubscriptionType subscriptionTypeResult = subscriptionTypeRepository.save(subscriptionType);

        if (subscriptionTypeResult != null) {
            SubscriptionTypeStatusResponseDTO result = new SubscriptionTypeStatusResponseDTO();
            result.setSubscriptionTypeId(subscriptionTypeResult.getSubscriptionTypeId());
            result.setStatus(Constants.status.CREATED.toString());
            return result;
        }

        return null;
    }

    public List<SubscriptionTypeDetailResponseDTO> getListSubscriptionType() {
        List<SubscriptionType> listSubscriptionTypeResult =
                subscriptionTypeRepository.findSubscriptionTypesByIsDeletedAndAppliedDateLessThanEqual(Boolean.FALSE, new Date().getTime());

        if (listSubscriptionTypeResult != null) {
            List<SubscriptionTypeDetailResponseDTO> result = new ArrayList<>();
            if (!listSubscriptionTypeResult.isEmpty()) {
                listSubscriptionTypeResult.sort(Comparator.comparing(SubscriptionType::getPrice));
                for (SubscriptionType subscriptionType : listSubscriptionTypeResult) {
                    SubscriptionTypeDetailResponseDTO dataResult = new SubscriptionTypeDetailResponseDTO();

                    dataResult.setSubscriptionTypeId(subscriptionType.getSubscriptionTypeId());
                    dataResult.setDescription(subscriptionType.getDescription());
                    dataResult.setName(subscriptionType.getName());
                    dataResult.setMaxChildren(subscriptionType.getMaxChildren());
                    dataResult.setMaxCollaborator(subscriptionType.getMaxCollaborator());
                    dataResult.setPrice(subscriptionType.getPrice());
                    dataResult.setDurationDay(subscriptionType.getDurationDay());
                    dataResult.setAppliedDate(subscriptionType.getAppliedDate());

                    result.add(dataResult);
                }
            }
            return result;
        }
        return null;
    }

    public SubscriptionTypeDetailResponseDTO getSubscriptionTypeDetail(Long subscriptionTypeId) {
        SubscriptionType subscriptionTypeResult =
                subscriptionTypeRepository.findSubscriptionTypeBySubscriptionTypeIdAndIsDeleted(subscriptionTypeId, Boolean.FALSE);
        if (subscriptionTypeResult != null) {
            SubscriptionTypeDetailResponseDTO result = new SubscriptionTypeDetailResponseDTO();

            result.setSubscriptionTypeId(subscriptionTypeResult.getSubscriptionTypeId());
            result.setName(subscriptionTypeResult.getName());
            result.setDescription(subscriptionTypeResult.getDescription());
            result.setMaxChildren(subscriptionTypeResult.getMaxChildren());
            result.setMaxCollaborator(subscriptionTypeResult.getMaxCollaborator());
            result.setPrice(subscriptionTypeResult.getPrice());
            result.setDurationDay(subscriptionTypeResult.getDurationDay());

            return result;
        }

        return null;
    }

    public ModifySubscriptionTypeResponseDTO modifySubscriptionType(SubscriptionType subscriptionType) {
        SubscriptionType subscriptionTypeResult = subscriptionTypeRepository.save(subscriptionType);
        if (subscriptionTypeResult != null) {
            ModifySubscriptionTypeResponseDTO result = new ModifySubscriptionTypeResponseDTO();

            result.setSubscriptionTypeId(subscriptionType.getSubscriptionTypeId());
            result.setName(subscriptionType.getName());
            result.setDescription(subscriptionType.getDescription());
            result.setMaxChildren(subscriptionType.getMaxChildren());
            result.setMaxCollaborator(subscriptionType.getMaxCollaborator());
            result.setPrice(subscriptionType.getPrice());
            result.setDurationDay(subscriptionType.getDurationDay());
            result.setStatus(Constants.status.UPDATED.toString());

            return result;
        }

        return null;
    }

    public SubscriptionType findSubscriptionTypeById(Long subscriptionTypeId) {
        return subscriptionTypeRepository.findSubscriptionTypeBySubscriptionTypeId(subscriptionTypeId);
    }

    public Subscription createFreeTrialSubscriptionForParentAccount(Subscription subscription) {
        return subscriptionRepository.save(subscription);
    }

    @Transactional
    public SubscriptionTypeStatusResponseDTO deleteSubscriptionType(SubscriptionType oldSubscriptionType, SubscriptionType newSubscriptionType) {
        oldSubscriptionType.setIsDeleted(Boolean.TRUE);

        SubscriptionType subscriptionTypeResult = subscriptionTypeRepository.save(oldSubscriptionType);
        if (subscriptionTypeResult != null) {
            SubscriptionTypeStatusResponseDTO result = new SubscriptionTypeStatusResponseDTO();

            result.setSubscriptionTypeId(subscriptionTypeResult.getSubscriptionTypeId());
            result.setStatus(Constants.status.DELETED.toString());

            replaceSubscriptionType(newSubscriptionType, oldSubscriptionType.getSubscriptionTypeId());

            return result;
        }

        return null;
    }

    public SubscriptionTypeStatusResponseDTO getSubscriptionTypeReplaceList(Long subscriptionTypeId) {
        SubscriptionType subscriptionTypeResult = subscriptionTypeRepository.getOne(subscriptionTypeId);
        if (subscriptionTypeResult != null) {
            SubscriptionTypeStatusResponseDTO result = new SubscriptionTypeStatusResponseDTO();

            result.setSubscriptionTypeId(subscriptionTypeResult.getSubscriptionTypeId());
            result.setStatus(Constants.status.DELETED.toString());

            List<SubscriptionType> subscriptionTypeReplaceList =
                    subscriptionTypeRepository.findSubscriptionTypesByMaxCollaboratorGreaterThanEqualAndMaxChildrenIsGreaterThanEqualAndAppliedDateLessThanEqualAndIsDeletedAndPriceNot(
                            subscriptionTypeResult.getMaxCollaborator(),
                            subscriptionTypeResult.getMaxChildren(),
                            new Date().getTime(),
                            Boolean.FALSE, new Double(0));
            if (subscriptionTypeReplaceList.contains(subscriptionTypeResult)) {
                subscriptionTypeReplaceList.remove(subscriptionTypeResult);
            }
            if (subscriptionTypeReplaceList != null) {
                List<SubscriptionTypeDetailResponseDTO> listReplaceResult = new ArrayList<>();
                if (!subscriptionTypeReplaceList.isEmpty()) {
                    for (SubscriptionType subsType : subscriptionTypeReplaceList) {
                        SubscriptionTypeDetailResponseDTO detailSusbcriptionType = new SubscriptionTypeDetailResponseDTO();

                        detailSusbcriptionType.setSubscriptionTypeId(subsType.getSubscriptionTypeId());
                        detailSusbcriptionType.setName(subsType.getName());
                        detailSusbcriptionType.setDescription(subsType.getDescription());
                        detailSusbcriptionType.setMaxCollaborator(subsType.getMaxCollaborator());
                        detailSusbcriptionType.setMaxChildren(subsType.getMaxChildren());
                        detailSusbcriptionType.setPrice(subsType.getPrice());
                        detailSusbcriptionType.setDurationDay(subsType.getDurationDay());
                        detailSusbcriptionType.setAppliedDate(subsType.getAppliedDate());

                        listReplaceResult.add(detailSusbcriptionType);
                    }
                    listReplaceResult.sort(Comparator.comparing(SubscriptionTypeDetailResponseDTO::getPrice));
                    result.setSubscriptionTypeReplace(listReplaceResult);
                }
            }
            return result;
        }

        return null;
    }

    @Transactional
    public SubscriptionTypeStatusResponseDTO replaceSubscriptionType(SubscriptionType newSubsType, Long oldSubsTypeId) {
        SubscriptionType oldSubsType = subscriptionTypeRepository.findSubscriptionTypeBySubscriptionTypeId(oldSubsTypeId);
        if (oldSubsType != null) {
            SubscriptionTypeStatusResponseDTO result = new SubscriptionTypeStatusResponseDTO();

            List<Subscription> subscriptionsList = oldSubsType.getSubscription().stream()
                    .filter(subs -> !subs.getParent().getIsDisabled().booleanValue())
                    .collect(Collectors.toList());

            ArrayList<String> pushToken = new ArrayList<>();

            for (Subscription subscription : subscriptionsList) {
                subscription.setSubscriptionType(newSubsType);
                Subscription subsResult = subscriptionRepository.save(subscription);

                if (subsResult != null) {
                    if (subscription.getParent().getPushToken() != null && !subscription.getParent().getPushToken().isEmpty()) {
                        pushToken.add(subscription.getParent().getPushToken());
                        String msg;

                        if (subscription.getParent().getLanguage().booleanValue()) {
                            msg = "Gói " + oldSubsType.getName() + " của bạn đã được nâng cấp lên gói " +
                                    subsResult.getSubscriptionType().getName() + ".";
                        } else {
                            msg = oldSubsType.getName() + " pack of your account is upgraded to pack " +
                                    subsResult.getSubscriptionType().getName() + ".";
                        }
                        notiService.pushNotificationMobile(msg
                                , result, pushToken);
                    }
                    result.setStatus("UPDATED");
                }
            }
            return result;
        }
        return null;
    }
}
