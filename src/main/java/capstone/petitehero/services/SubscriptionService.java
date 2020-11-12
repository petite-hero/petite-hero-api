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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    private SubscriptionTypeRepository subscriptionTypeRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

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
                subscriptionTypeRepository.findSubscriptionTypesByIsDeleted(Boolean.FALSE);

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

    public SubscriptionTypeStatusResponseDTO deleteSubscriptionType(SubscriptionType subscriptionType) {
        subscriptionType.setIsDeleted(Boolean.TRUE);

        SubscriptionType subscriptionTypeResult = subscriptionTypeRepository.save(subscriptionType);
        if (subscriptionTypeResult != null) {
            SubscriptionTypeStatusResponseDTO result = new SubscriptionTypeStatusResponseDTO();

            result.setSubscriptionTypeId(subscriptionTypeResult.getSubscriptionTypeId());
            result.setStatus(Constants.status.DELETED.toString());

            return result;
        }

        return null;
    }
}
