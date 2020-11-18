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
import capstone.petitehero.entities.Account;
import capstone.petitehero.entities.Child;
import capstone.petitehero.entities.Parent_Child;
import capstone.petitehero.exceptions.DuplicateKeyException;
import capstone.petitehero.repositories.AccountRepository;
import capstone.petitehero.utilities.JWTUtil;
import capstone.petitehero.utilities.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PetiteHeroUserDetailService petiteHeroUserDetailService;

    @Autowired
    private AuthenticationManager authenticationManager;

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
        Account account = accountRepository.findAdminByUsernameEqualsAndPasswordEquals(accountLoginDTO.getUsername(), accountLoginDTO.getPassword());
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
                    result.setJwt(token);
                    result.setRole(Constants.PARENT);
                    result.setPhoneNumber(account.getUsername());
                } else {
                    result.setRole(Constants.ADMIN);
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

                    resultData.setName(account.getParent().getName());
                    resultData.setEmail(account.getParent().getEmail());
                    resultData.setPhoneNumber(account.getUsername());
                    resultData.setExpiredDate(account.getParent().getSubscription().getExpiredDate());
                    resultData.setSubscriptionType(account.getParent().getSubscription().getSubscriptionType().getName());
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
            result.setAvatar(Util.fromImageFileToBase64String(account.getParent().getPhoto()));
            result.setPhoneNumber(account.getUsername());
            result.setExpiredDate(account.getParent().getSubscription().getExpiredDate());
            result.setSubscriptionType(account.getParent().getSubscription().getSubscriptionType().getName());
            if (account.getParent().getPhoto() != null && !account.getParent().getPhoto().isEmpty()) {
                result.setAvatar(Util.fromImageFileToBase64String(account.getParent().getPhoto()));
            }

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
            }

            return result;
        }
        return null;
    }
}
