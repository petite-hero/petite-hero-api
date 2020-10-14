package capstone.petitehero.services;

import capstone.petitehero.config.jwt.PetiteHeroUserDetailService;
import capstone.petitehero.dtos.request.admin.AccountLoginDTO;
import capstone.petitehero.dtos.response.account.AccountLoginResponseDTO;
import capstone.petitehero.entities.Account;
import capstone.petitehero.exceptions.DuplicateKeyException;
import capstone.petitehero.repositories.AccountRepository;
import capstone.petitehero.utilities.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

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
                result.setRole("Admin");
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

    public String loginAccount(AccountLoginDTO accountLoginDTO) {
        Account account = accountRepository.findAdminByUsernameEqualsAndPasswordEquals(accountLoginDTO.getUsername(), accountLoginDTO.getPassword());
        if (account != null) {
            try {
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(account.getUsername(), account.getUsername())
                );
                JWTUtil jwtUtil = new JWTUtil();

                UserDetails userDetails = petiteHeroUserDetailService.loadUserByUsername(account.getUsername());

                return jwtUtil.generateToken(userDetails);
            } catch (Exception e) {
                return "Server down";
            }
        }
        return null;
    }
}
