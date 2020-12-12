package capstone.petitehero.services;

import capstone.petitehero.config.common.Constants;
import capstone.petitehero.config.jwt.PetiteHeroUserDetailService;
import capstone.petitehero.dtos.common.ChildInformation;
import capstone.petitehero.dtos.common.ParentInformation;
import capstone.petitehero.dtos.request.admin.AccountLoginDTO;
import capstone.petitehero.dtos.response.account.AccountLoginResponseDTO;
import capstone.petitehero.dtos.response.account.ListParentAccountResponseDTO;
import capstone.petitehero.dtos.response.account.LoginResponseDTO;
import capstone.petitehero.dtos.response.account.ParentDetailResponseDTO;
import capstone.petitehero.dtos.response.subscription.ListSubscriptionResponseDTO;
import capstone.petitehero.entities.Account;
import capstone.petitehero.entities.Parent;
import capstone.petitehero.entities.Parent_Child;
import capstone.petitehero.entities.Subscription;
import capstone.petitehero.exceptions.DuplicateKeyException;
import capstone.petitehero.repositories.AccountRepository;
import capstone.petitehero.repositories.ParentRepository;
import capstone.petitehero.repositories.SubscriptionRepository;
import capstone.petitehero.utilities.JWTUtil;
import capstone.petitehero.utilities.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

import java.util.Date;
import java.util.List;

import java.util.stream.Collectors;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PetiteHeroUserDetailService petiteHeroUserDetailService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    public AccountLoginResponseDTO registerByAdmin(Account account) throws DuplicateKeyException {
        if (accountRepository.existsAccountByUsername(account.getUsername())) {
            throw new DuplicateKeyException("Username already exists in the system");
        } else {
            Account accountResult = accountRepository.save(account);

            if (accountResult != null) {
                AccountLoginResponseDTO result = new AccountLoginResponseDTO();
                result.setRole(Constants.ADMIN);
                return result;
            }
            return null;
        }
    }

    public Account registerByParent(Account account) throws DuplicateKeyException {
        if (accountRepository.existsAccountByUsername(account.getUsername())) {
            throw new DuplicateKeyException("Phone number already exists in the system");
        } else {
            Account accountResult = accountRepository.save(account);

            if (accountResult != null) {
                return accountResult;
            }
            return null;
        }
    }

    public Account findAccountByUsername(String username) {
        return accountRepository.findAccountByUsername(username);
    }

    public String changeAccountPassword(Account account) {
        Account accountResult = accountRepository.save(account);
        if (accountResult != null) {
            return "Password of account has been updated";
        }
        return null;
    }

    public LoginResponseDTO loginAccount(AccountLoginDTO accountLoginDTO) {
        Account account = accountRepository.findAccountByUsernameAndPassword(accountLoginDTO.getUsername(), accountLoginDTO.getPassword());
        if (account != null) {
            try {
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(account.getUsername(), account.getUsername())
                );
                JWTUtil jwtUtil = new JWTUtil();

                UserDetails userDetails = petiteHeroUserDetailService.loadUserByUsername(account.getUsername());

                String token = jwtUtil.generateToken(userDetails);

                LoginResponseDTO result = new LoginResponseDTO();
                if (account.getRole().equals(Constants.PARENT)) {
                    // check account is expired or disabled.
                    Subscription parentCurrentSubscription = subscriptionRepository.findSubscriptionByParent_Account_UsernameAndIsDisabledAndAndExpiredDateAfter(
                            account.getUsername(),
                            Boolean.FALSE,
                            new Date().getTime());
                    if (parentCurrentSubscription != null) {
                        if (parentCurrentSubscription.getExpiredDate() < new Date().getTime()) {
                            result.setIsExpired(Boolean.TRUE);
                        } else {
                            result.setIsExpired(Boolean.FALSE);
                        }
                    }
                    if (!account.getParent().getIsDisabled().booleanValue()) {
                        result.setJwt(token);
                        result.setRole(account.getRole());
                        result.setPhoneNumber(account.getUsername());
                        if (account.getParent().getLanguage() != null && !account.getParent().getLanguage().toString().isEmpty()) {
                            if (account.getParent().getLanguage().booleanValue()) {
                                result.setLanguage("vi");
                            } else {
                                result.setLanguage("en");
                            }
                        }
                        result.setIsVerify(account.getParent().getIsVerify());
                    }
                    result.setIsDisabled(account.getParent().getIsDisabled().booleanValue());
                } else {
                    result.setRole(account.getRole());
                    result.setPhoneNumber(account.getUsername());
                    result.setJwt(token);
                }
                return result;
            } catch (UsernameNotFoundException usernameNotFoundException) {
                return null;
            }
        }
        return null;
    }

    public List<ListParentAccountResponseDTO> listAllParentAccountForAdmin() {
        List<ListParentAccountResponseDTO> result = new ArrayList<>();

        List<Account> parentAccountList = accountRepository.findAccountsByRole(Constants.PARENT);
        if (parentAccountList != null) {
            if (!parentAccountList.isEmpty()) {
                for (Account account : parentAccountList) {
                    ListParentAccountResponseDTO resultData = new ListParentAccountResponseDTO();

                    Subscription parentCurrentSubscription = subscriptionRepository.findSubscriptionByParent_Account_UsernameAndIsDisabledAndAndExpiredDateAfter(
                            account.getUsername(),
                            Boolean.FALSE,
                            new Date().getTime());

                    resultData.setName(account.getParent().getName());
                    resultData.setEmail(account.getParent().getEmail());
                    resultData.setPhoneNumber(account.getUsername());
                    if (parentCurrentSubscription != null) {
                        resultData.setExpiredDate(parentCurrentSubscription.getExpiredDate());
                        resultData.setSubscriptionType(parentCurrentSubscription.getSubscriptionType().getName());
                    }
                    resultData.setIsDisable(account.getParent().getIsDisabled());

                    result.add(resultData);
                }
            }
        }
        return result;
    }

    public ParentDetailResponseDTO getDetailOfParentAccount(String username) {
        Account account = accountRepository.findAccountByUsernameAndAndRole(
                username, Constants.PARENT);
        if (account != null) {
            ParentDetailResponseDTO result = new ParentDetailResponseDTO();

            result.setName(account.getParent().getName());
            result.setEmail(account.getParent().getEmail());
            result.setPhoneNumber(account.getUsername());
            Subscription parentCurrentSubscription = subscriptionRepository.findSubscriptionByParent_Account_UsernameAndIsDisabledAndAndExpiredDateAfter(
                    account.getUsername(),
                    Boolean.FALSE,
                    new Date().getTime());

            if (parentCurrentSubscription != null) {
                result.setExpiredDate(parentCurrentSubscription.getExpiredDate());
                result.setSubscriptionType(parentCurrentSubscription.getSubscriptionType().getName());
            }
//            if (account.getParent().getPhoto() != null && !account.getParent().getPhoto().isEmpty()) {
//                result.setAvatar(Util.fromImageFileToBase64String(account.getParent().getPhoto()));
//            }

            //get list children active of active parent
            if (account.getParent().getParent_childCollection() != null) {
                ArrayList<ChildInformation> childInformationList = new ArrayList<>();
                if (!account.getParent().getParent_childCollection().isEmpty()) {
                    List<Parent_Child> children = account.getParent().getParent_childCollection()
                            .stream()
                            .filter(Util.distinctByKey(Parent_Child::getChild))
                            .filter(parent_child -> !parent_child.getChild().getIsDisabled().booleanValue())
                            .collect(Collectors.toList());
                    for (Parent_Child parent_child : children) {
                        ChildInformation childInformation = new ChildInformation();
                        childInformation.setName(parent_child.getChild().getName());
                        childInformation.setNickName(parent_child.getChild().getNickName());
                        childInformation.setYob(parent_child.getChild().getYob());
                        if (parent_child.getChild().getGender().booleanValue()) {
                            childInformation.setGender("Male");
                        } else {
                            childInformation.setGender("Female");
                        }

                        childInformationList.add(childInformation);
                    }
                    result.setChildInformationList(childInformationList);
                } else {
                    result.setChildInformationList(new ArrayList<>());
                }
                result.setMaxChild(result.getChildInformationList().size());
            }

            //get list collaborator active of active parent
            if (account.getParent().getParent_collaboratorCollection() != null) {
                ArrayList<ParentInformation> collaboratorInformationList = new ArrayList<>();
                if (!account.getParent().getParent_collaboratorCollection().isEmpty()) {
                    List<Parent_Child> collaborator = account.getParent().getParent_childCollection().stream()
                            .filter(parent_child -> parent_child.getCollaborator() != null)
                            .filter(Util.distinctByKey(Parent_Child::getCollaborator))
                            .collect(Collectors.toList());
                    for (Parent_Child parent_child : collaborator) {
                        ParentInformation collaboratorInformation = new ParentInformation();

                        collaboratorInformation.setPhoneNumber(parent_child.getCollaborator().getAccount().getUsername());
                        collaboratorInformation.setName(parent_child.getCollaborator().getName());
                        collaboratorInformation.setEmail(parent_child.getCollaborator().getEmail());
                        if (parent_child.getCollaborator().getGender().booleanValue()) {
                            collaboratorInformation.setGender("Male");
                        } else {
                            collaboratorInformation.setGender("Female");
                        }
                        collaboratorInformationList.add(collaboratorInformation);
                    }
                    result.setCollaboratorInformationList(collaboratorInformationList);
                } else {
                    result.setCollaboratorInformationList(collaboratorInformationList);
                }
                result.setMaxCollaborator(result.getCollaboratorInformationList().size());
            }

            // get all subscriptions of parent account
            if (account.getParent().getParent_subscriptionCollection() != null) {
                ArrayList<ListSubscriptionResponseDTO> subscriptionHistoryList = new ArrayList<>();
                if (!account.getParent().getParent_subscriptionCollection().isEmpty()) {
                    for (Subscription historySubscription : account.getParent().getParent_subscriptionCollection()) {
                        ListSubscriptionResponseDTO dataResult = new ListSubscriptionResponseDTO();

                        dataResult.setSubscriptionId(historySubscription.getSubscriptionId());
                        dataResult.setStartDate(historySubscription.getStartDate());
                        dataResult.setIsDisabled(historySubscription.getIsDisabled());
                        dataResult.setExpiredDate(historySubscription.getExpiredDate());
                        dataResult.setName(historySubscription.getSubscriptionType().getName());

                        subscriptionHistoryList.add(dataResult);
                    }
                    result.setSubscriptionHistoryList(subscriptionHistoryList);
                } else {
                    result.setSubscriptionHistoryList(subscriptionHistoryList);
                }
            }

            return result;
        }
        return null;
    }

    public String resetPasswordForParentAccount(Parent parent, String password) throws InvalidKeySpecException, NoSuchAlgorithmException {
        parent.getAccount().setPassword(Util.encodePassword(password));

        Parent parentResult = parentRepository.save(parent);
        if (parentResult != null) {
            String result = Constants.status.UPDATED.toString();

            return result;
        }
        return null;
    }
}
