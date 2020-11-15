package capstone.petitehero.services;

import capstone.petitehero.config.common.Constants;
import capstone.petitehero.config.jwt.PetiteHeroUserDetailService;
import capstone.petitehero.dtos.request.admin.AccountLoginDTO;
import capstone.petitehero.dtos.response.account.AccountLoginResponseDTO;
import capstone.petitehero.dtos.response.account.ListParentAccountResponseDTO;
import capstone.petitehero.dtos.response.account.LoginResponseDTO;
import capstone.petitehero.dtos.response.account.ParentDetailResponseDTO;
import capstone.petitehero.entities.Account;
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

import java.util.ArrayList;
import java.util.List;

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
                    result.setPhoneNumber(account.getUsername());
                } else {
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

        List<Account> parentAccountList = accountRepository.findAccountsByParent_IsDisabledAndRole(Boolean.FALSE, Constants.PARENT);
        if (parentAccountList != null) {
            if (!parentAccountList.isEmpty()) {
                for (Account account : parentAccountList) {
                    ListParentAccountResponseDTO resultData = new ListParentAccountResponseDTO();

                    resultData.setName(account.getParent().getName());
                    resultData.setEmail(account.getParent().getEmail());
                    resultData.setPhoneNumber(account.getUsername());
                    resultData.setExpiredDate(account.getParent().getSubscription().getExpiredDate());
                    resultData.setSubscriptionType(account.getParent().getSubscription().getSubscriptionType().getName());

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
            result.setExpiredDate(account.getParent().getSubscription().getExpiredDate());
            result.setSubscriptionType(account.getParent().getSubscription().getSubscriptionType().getName());
            if (account.getParent().getPhoto() != null && !account.getParent().getPhoto().isEmpty()) {
                result.setAvatar(Util.fromImageFileToBase64String(account.getParent().getPhoto()));
            }

            //get list children active of active parent
            if (account.getParent().getParent_childCollection() != null) {
                if (!account.getParent().getParent_childCollection().isEmpty()) {
                    Long countActiveChildren = account.getParent().getParent_childCollection().stream()
                            .filter(Util.distinctByKey(Parent_Child::getChild))
                            .filter(parent_child -> !parent_child.getChild().getIsDisabled().booleanValue())
                            .count();
                    result.setListActiveChildren(countActiveChildren.intValue());
                } else {
                    result.setListActiveChildren(new Integer(0));
                }
            }

            //get list collaborator active of active parent
            if (account.getParent().getParent_collaboratorCollection() != null) {
                if (!account.getParent().getParent_collaboratorCollection().isEmpty()) {
                    Long countCollaborator = account.getParent().getParent_collaboratorCollection().stream()
                            .filter(Util.distinctByKey(Parent_Child::getCollaborator))
                            .count();
                    result.setListCollaborator(countCollaborator.intValue());
                } else {
                    result.setListCollaborator(new Integer(0));
                }
            }

            return result;
        }
        return null;
    }
}
