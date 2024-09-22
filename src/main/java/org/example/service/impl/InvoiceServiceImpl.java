package org.example.service.impl;

import org.example.dto.invoice.InvoiceCreateDTO;
import org.example.exception.InvoiceNotFoundException;
import org.example.model.Invoice;
import org.example.repo.InvoiceRepository;
import org.example.service.IInvoiceService;
import org.example.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InvoiceServiceImpl implements IInvoiceService {

    @Autowired
    private InvoiceRepository repo;
    @Autowired
    private StorageService storageService;

    @Override
    public Invoice saveInvoice(InvoiceCreateDTO dto) {
        try {
            Invoice invoice = new Invoice();
            invoice.setName(dto.getName());
            invoice.setLocation(dto.getLocation());
            invoice.setAmount(dto.getAmount());
            var fileName = storageService.save(dto.getFile());
            invoice.setFileName(fileName);
            return repo.save(invoice);
        } catch (Exception ex)
        {
            return null;
        }
    }

    @Override
    public List<Invoice> getAllInvoices() {
        return repo.findAll();
    }

    @Override
    public Invoice getInvoiceById(Long id) {
        Optional<Invoice> opt = repo.findById(id);
        if(opt.isPresent()) {
            return opt.get();
        } else {
            throw new InvoiceNotFoundException("Invoice with Id : "+id+" Not Found");
        }
    }

    @Override
    public void deleteInvoiceById(Long id) {
        repo.delete(getInvoiceById(id));
    }

    @Override
    public void updateInvoice(Invoice invoice) {
        repo.save(invoice);
    }
}