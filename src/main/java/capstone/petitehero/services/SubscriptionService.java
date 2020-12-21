package capstone.petitehero.services;

import capstone.petitehero.config.common.Constants;
import capstone.petitehero.dtos.common.ParentInformation;
import capstone.petitehero.dtos.response.subscription.ListSubscriptionResponseDTO;
import capstone.petitehero.dtos.response.subscription.type.SubscriptionTypeStatusResponseDTO;
import capstone.petitehero.dtos.response.subscription.type.SubscriptionTypeDetailResponseDTO;
import capstone.petitehero.entities.Parent;
import capstone.petitehero.entities.Subscription;
import capstone.petitehero.entities.SubscriptionType;
import capstone.petitehero.repositories.ParentRepository;
import capstone.petitehero.repositories.SubscriptionRepository;
import capstone.petitehero.repositories.SubscriptionTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SubscriptionService {

    @Autowired
    private SubscriptionTypeRepository subscriptionTypeRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private NotificationService notiService;

    public SubscriptionTypeStatusResponseDTO createNewSubscriptionType(SubscriptionType subscriptionType) {
        Integer countSubsType = subscriptionTypeRepository.countAllBySubscriptionTypeIdIsNotAndIsDeleted(Constants.FREE_TRAIL_TYPE, Boolean.FALSE);

        if (countSubsType != null) {
            if (countSubsType < 3 && countSubsType >= 0) {
                SubscriptionType subscriptionTypeResult = subscriptionTypeRepository.save(subscriptionType);

                if (subscriptionTypeResult != null) {
                    SubscriptionTypeStatusResponseDTO result = new SubscriptionTypeStatusResponseDTO();
                    result.setSubscriptionTypeId(subscriptionTypeResult.getSubscriptionTypeId());
                    result.setStatus(Constants.status.CREATED.toString());
                    return result;
                }
            } else {
                SubscriptionTypeStatusResponseDTO result = new SubscriptionTypeStatusResponseDTO();
                result.setStatus(Constants.status.FAILED.toString());
                return result;
            }
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

    public SubscriptionType findSubscriptionTypeById(Long subscriptionTypeId) {
        return subscriptionTypeRepository.findSubscriptionTypeBySubscriptionTypeId(subscriptionTypeId);
    }

    public Subscription saveSubscriptionForParent(Subscription subscription) {
        return subscriptionRepository.save(subscription);
    }

    @Transactional
    public Subscription disableOldSubscriptionAndSaveNewSubscriptionForParent(Subscription oldSubscription, Subscription newSubscription) {
        Subscription newSubscriptionResult = subscriptionRepository.save(newSubscription);
        if (newSubscriptionResult != null) {
            Subscription oldSubscriptionResult = subscriptionRepository.save(oldSubscription);
            if (oldSubscriptionResult != null) {
                return newSubscriptionResult;
            }
            return null;
        }
        return null;
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
                    .filter(subs -> !subs.getIsDisabled().booleanValue())
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

    public Subscription findParentCurrentSubscription(Parent parent) {
        Subscription result = subscriptionRepository.findSubscriptionByParent_Account_UsernameAndIsDisabledAndAndExpiredDateAfter(
                parent.getAccount().getUsername(),
                Boolean.FALSE,
                new Date().getTime());

        if (result != null) {
            return result;
        }

        return null;
    }

    public Parent updateParentSubscription(Parent parent, Subscription parentCurrentSubscription, SubscriptionType subscriptionType) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, subscriptionType.getDurationDay());

        if (parentCurrentSubscription.getSubscriptionType().getSubscriptionTypeId().longValue() ==
                subscriptionType.getSubscriptionTypeId().longValue()) {

            parentCurrentSubscription.setExpiredDate(calendar.getTime().getTime());
        } else {
            parentCurrentSubscription.setIsDisabled(Boolean.TRUE);
            Subscription subscription = new Subscription();

            subscription.setStartDate(new Date().getTime());
            subscription.setExpiredDate(calendar.getTime().getTime());
            subscription.setIsDisabled(Boolean.FALSE);
            subscription.setSubscriptionType(subscriptionType);
            subscription.setParent(parent);
        }

        Parent parentResult = parentRepository.save(parent);
        if (parentResult != null) {
            return parentResult;
        }
        return null;
    }

    public List<ListSubscriptionResponseDTO> getAllSubscriptionForAdmin() {
        List<Subscription> listSubscription = subscriptionRepository.findAll();
        if (listSubscription != null) {
            List<ListSubscriptionResponseDTO> result = new ArrayList<>();
            if (!listSubscription.isEmpty()) {
                for (Subscription subscription : listSubscription) {
                    ListSubscriptionResponseDTO dataResult = new ListSubscriptionResponseDTO();
                    dataResult.setSubscriptionId(subscription.getSubscriptionId());
                    dataResult.setExpiredDate(subscription.getExpiredDate());
                    dataResult.setIsDisabled(subscription.getIsDisabled());
                    dataResult.setStartDate(subscription.getStartDate());
                    dataResult.setName(subscription.getSubscriptionType().getName());

                    ParentInformation parentInformation = new ParentInformation();
                    parentInformation.setName(subscription.getParent().getName());
                    parentInformation.setPhoneNumber(subscription.getParent().getAccount().getUsername());
                    parentInformation.setEmail(subscription.getParent().getEmail());

                    dataResult.setParentInformation(parentInformation);

                    result.add(dataResult);
                }
            }
            return result;
        }
        return null;
    }
}
