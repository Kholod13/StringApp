package org.example.controllers;

import org.example.exception.InvoiceNotFoundException;
import org.example.model.Invoice;
import org.example.service.IInvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Controller
@RequestMapping("/invoice")
public class InvoiceController {

    @Autowired
    private IInvoiceService service;

    @GetMapping("/")
    public String showHomePage() {
        return "homePage";
    }

    @GetMapping("/register")
    public String showRegistration() {
        return "registerInvoicePage";
    }

    @PostMapping("/save")
    public String saveInvoice(
            @ModelAttribute Invoice invoice,
            @RequestParam("photo") MultipartFile photo,
            RedirectAttributes attributes
    ) {
        try {
            // Перевірка чи завантажений файл є зображенням
            if (photo.isEmpty() || !photo.getContentType().startsWith("image/")) {
                throw new IllegalArgumentException("Invalid file type. Please upload an image.");
            }

            // Зберігаємо фото на сервері
            String photoName = savePhoto(photo);
            invoice.setPhotoName(photoName);

            // Зберігаємо Invoice
            Long id = service.saveInvice(invoice).getId();
            attributes.addAttribute("message", "Record with id: '" + id + "' saved successfully!");

        } catch (IOException e) {
            attributes.addAttribute("message", "Error saving photo: " + e.getMessage());
            return "redirect:/invoice/register";
        } catch (IllegalArgumentException e) {
            attributes.addAttribute("message", e.getMessage());
            return "redirect:/invoice/register";
        }

        return "redirect:getAllInvoices";
    }

    @GetMapping("/getAllInvoices")
    public String getAllInvoices(
            @RequestParam(value = "message", required = false) String message,
            Model model
    ) {
        List<Invoice> invoices= service.getAllInvoices();
        model.addAttribute("list", invoices);
        model.addAttribute("message", message);
        return "allInvoicesPage";
    }

    @GetMapping("/edit")
    public String getEditPage(
            Model model,
            RedirectAttributes attributes,
            @RequestParam Long id
    ) {
        String page = null;
        try {
            Invoice invoice = service.getInvoiceById(id);
            model.addAttribute("invoice", invoice);
            page="editInvoicePage";
        } catch (InvoiceNotFoundException e) {
            e.printStackTrace();
            attributes.addAttribute("message", e.getMessage());
            page="redirect:getAllInvoices";
        }
        return page;
    }

    @PostMapping("/update")
    public String updateInvoice(
            @ModelAttribute Invoice invoice,
            @RequestParam("photo") MultipartFile newPhoto,
            RedirectAttributes attributes
    ) {
        try {
            Invoice existingInvoice = service.getInvoiceById(invoice.getId());

            // Якщо нове фото не завантажене, залишаємо старе
            if (!newPhoto.isEmpty() && newPhoto.getContentType().startsWith("image/")) {
                // Видаляємо старе фото
                deletePhoto(existingInvoice.getPhotoName());

                // Зберігаємо нове фото
                String newPhotoName = savePhoto(newPhoto);
                invoice.setPhotoName(newPhotoName);
            } else {
                // Якщо нове фото не завантажене, залишаємо старе
                invoice.setPhotoName(existingInvoice.getPhotoName());
            }

            // Оновлюємо Invoice
            service.updateInvoice(invoice);
            attributes.addAttribute("message", "Invoice with id: '" + invoice.getId() + "' updated successfully!");
        } catch (IOException e) {
            attributes.addAttribute("message", "Error updating photo: " + e.getMessage());
            return "redirect:getAllInvoices";
        } catch (InvoiceNotFoundException e) {
            attributes.addAttribute("message", e.getMessage());
            return "redirect:getAllInvoices";
        }

        return "redirect:getAllInvoices";
    }


    @GetMapping("/delete")
    public String deleteInvoice(
            @RequestParam Long id,
            RedirectAttributes attributes
    ) {
        try {
            Invoice invoice = service.getInvoiceById(id);

            // Видаляємо фото, якщо воно є
            if (invoice.getPhotoName() != null) {
                deletePhoto(invoice.getPhotoName());
            }

            // Видаляємо інвойс з бази даних
            service.deleteInvoiceById(id);
            attributes.addAttribute("message", "Invoice with Id: '" + id + "' is removed successfully!");

        } catch (InvoiceNotFoundException e) {
            e.printStackTrace();
            attributes.addAttribute("message", e.getMessage());
        }

        return "redirect:getAllInvoices";
    }

    private String savePhoto(MultipartFile photo) throws IOException {
        String fileName = System.currentTimeMillis() + "_" + photo.getOriginalFilename();
        Path uploadPath = Paths.get("uploads");

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(fileName);
        Files.copy(photo.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }

    private void deletePhoto(String photoName) throws IOException {
        Path filePath = Paths.get("uploads").resolve(photoName);

        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
    }


}