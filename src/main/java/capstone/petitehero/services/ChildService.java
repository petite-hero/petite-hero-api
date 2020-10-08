package capstone.petitehero.services;

import capstone.petitehero.dtos.response.child.AddChildResponseDTO;
import capstone.petitehero.entities.Child;
import capstone.petitehero.entities.Parent;
import capstone.petitehero.entities.Parent_Child;
import capstone.petitehero.repositories.ChildRepository;
import capstone.petitehero.repositories.ParentChildRepository;
import capstone.petitehero.repositories.ParentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChildService {

    @Autowired
    private ChildRepository childRepository;

    @Autowired
    private ParentChildRepository parentChildRepository;

    @Autowired
    private ParentRepository parentRepository;

    public AddChildResponseDTO addChildForParent(Child child, String parentPhoneNumber) {
        Child childResult = childRepository.save(child);
        AddChildResponseDTO result = new AddChildResponseDTO();
        if (childResult != null) {
            result.setFirstName(childResult.getFirstName());
            result.setLastName(childResult.getLastName());
            result.setNickName(childResult.getNickName());
            result.setPhoto(childResult.getPhoto());
            if (childResult.getLanguage() != null) {
                if (childResult.getLanguage().booleanValue()) {
                    result.setLanguage("Vietnamese");
                    if (childResult.getGender() != null) {
                        if (childResult.getGender().booleanValue()) {
                            result.setGender("Nam");
                        } else {
                            result.setGender("Nữ");
                        }
                    }
                } else {
                    result.setLanguage("English");
                    if (childResult.getGender() != null) {
                        if (childResult.getGender().booleanValue()) {
                            result.setGender("Male");
                        } else {
                            result.setGender("Female");
                        }
                    }
                }
            }
            Parent parentResult = parentRepository.findParentByParentPhoneNumberEquals(parentPhoneNumber);
            Parent_Child parent_child = new Parent_Child();
            parent_child.setChild(childResult);
            parent_child.setParent(parentResult);
            parentChildRepository.save(parent_child);
            return result;
        }
        return null;
    }
}
