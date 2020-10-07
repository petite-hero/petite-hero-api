package capstone.petitehero.services;

import capstone.petitehero.config.jwt.PetiteHeroUserDetailService;
import capstone.petitehero.dtos.user.UserLoginDTO;
import capstone.petitehero.entities.Admin;
import capstone.petitehero.repositories.AdminRepository;
import capstone.petitehero.util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PetiteHeroUserDetailService petiteHeroUserDetailService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public Admin register(Admin loginAdminDTO) {
        return adminRepository.save(loginAdminDTO);
    }

    public String loginByUser(UserLoginDTO userLoginDTO) {
        Admin admin = adminRepository.findAdminByUsernameEqualsAndPasswordEquals(userLoginDTO.getUsername(), userLoginDTO.getPassword());
        if (admin != null) {
            try {
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(admin.getUsername(), admin.getUsername())
                );
                JWTUtil jwtUtil = new JWTUtil();

                UserDetails userDetails = petiteHeroUserDetailService.loadUserByUsername(admin.getUsername());

                return jwtUtil.generateToken(userDetails);
            } catch (Exception e) {
                return "Server down";
            }
        }
        return null;
    }
}
