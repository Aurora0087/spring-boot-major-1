package com.ss.tst1.videoContentCategory;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class CategoryService {

    @Autowired
    private CategoryRepo categoryRepo;

    public ResponseEntity<String> createCategory(String name){

        if (isCategoryExist(name)){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Category already exist.");
        }

        try{

            Category newCategory= new Category(name);
            categoryRepo.save(newCategory);

            return ResponseEntity.ok("New Category '"+name+"' added.");
        }catch (Error e){
            throw new RuntimeException(e);
        }
    }

    public Optional<Category> getCategory(Integer categoryId){
        return categoryRepo.findById(categoryId);
    }

    public Boolean isCategoryExist(Integer categoryId){
        Optional<Category> category = categoryRepo.findById(categoryId);
        return category.isPresent();
    }

    public Boolean isCategoryExist(String name){
        Optional<Category> category = categoryRepo.findByCategoryName(name);
        return category.isPresent();
    }
}
