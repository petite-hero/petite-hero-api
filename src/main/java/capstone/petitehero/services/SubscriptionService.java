package capstone.petitehero.services;

import capstone.petitehero.dtos.response.subscription.type.CreateSubscriptionTypeResponseDTO;
import capstone.petitehero.dtos.response.subscription.type.ListSubscriptionTypeResponseDTO;
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
import java.util.stream.Collectors;

@Service
public class SubscriptionService {

    @Autowired
    private SubscriptionTypeRepository subscriptionTypeRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    public CreateSubscriptionTypeResponseDTO createNewSubscriptionType(SubscriptionType subscriptionType) {
        SubscriptionType subscriptionTypeResult = subscriptionTypeRepository.save(subscriptionType);

        if (subscriptionTypeResult != null) {
            CreateSubscriptionTypeResponseDTO result = new CreateSubscriptionTypeResponseDTO();
            result.setNewSubscriptionTypeId(subscriptionTypeResult.getSubscriptionTypeId());
            result.setStatus("CREATED");
            return result;
        }

        return null;
    }

    public List<ListSubscriptionTypeResponseDTO> getListSubscriptionType() {
        List<SubscriptionType> listSubscriptionTypeResult = subscriptionTypeRepository.findAll().stream()
                .sorted(Comparator.comparing(SubscriptionType::getPrice))
                .collect(Collectors.toList());

        if (listSubscriptionTypeResult != null) {
            List<ListSubscriptionTypeResponseDTO> result = new ArrayList<>();
            for (SubscriptionType subscriptionType : listSubscriptionTypeResult) {
                ListSubscriptionTypeResponseDTO dataResult = new ListSubscriptionTypeResponseDTO();

                dataResult.setSubscriptionTypeId(subscriptionType.getSubscriptionTypeId());
                dataResult.setDescription(subscriptionType.getDescription());
                dataResult.setName(subscriptionType.getName());
                dataResult.setMaxChildren(subscriptionType.getMaxChildren());
                dataResult.setMaxCollaborator(subscriptionType.getMaxCollaborator());
                dataResult.setPrice(subscriptionType.getPrice());

                result.add(dataResult);
            }
            return result;
        }

        return null;
    }

    public SubscriptionTypeDetailResponseDTO getSubscriptionTypeDetail(Long subscriptionTypeId) {
        SubscriptionType subscriptionTypeResult = subscriptionTypeRepository.findById(subscriptionTypeId).get();
        if (subscriptionTypeResult != null) {
            SubscriptionTypeDetailResponseDTO result = new SubscriptionTypeDetailResponseDTO();

            result.setSubscriptionTypeId(subscriptionTypeResult.getSubscriptionTypeId());
            result.setName(subscriptionTypeResult.getName());
            result.setDescription(subscriptionTypeResult.getDescription());
            result.setMaxChildren(subscriptionTypeResult.getMaxChildren());
            result.setMaxCollaborator(subscriptionTypeResult.getMaxCollaborator());
            result.setPrice(subscriptionTypeResult.getPrice());

            return result;
        }

        return null;
    }

    public ModifySubscriptionTypeResponseDTO modifySubscriptionType(Long subscriptionTypeId, SubscriptionType subscriptionType) {
        SubscriptionType subscriptionTypeResult = subscriptionTypeRepository.findById(subscriptionTypeId).get();
        if (subscriptionTypeResult != null) {
            ModifySubscriptionTypeResponseDTO result = new ModifySubscriptionTypeResponseDTO();

            result.setSubscriptionTypeId(subscriptionTypeResult.getSubscriptionTypeId());
            result.setName(subscriptionTypeResult.getName());
            result.setDescription(subscriptionTypeResult.getDescription());
            result.setMaxChildren(subscriptionTypeResult.getMaxChildren());
            result.setMaxCollaborator(subscriptionTypeResult.getMaxCollaborator());
            result.setPrice(subscriptionTypeResult.getPrice());
            result.setStatus("UPDATED");

            return result;
        }

        return null;
    }

    public SubscriptionType findSubscriptionTypeById(Long subscriptionTypeId) {
        return subscriptionTypeRepository.findById(subscriptionTypeId).get();
    }

    public Subscription createFreeTrialSubscriptionForParentAccount(Subscription subscription) {
        return subscriptionRepository.save(subscription);
    }
}
