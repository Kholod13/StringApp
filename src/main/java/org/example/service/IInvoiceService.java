package org.example.service;

import org.example.dto.invoice.InvoiceCreateDTO;
import org.example.model.Invoice;

import java.util.List;

public interface IInvoiceService {
    public Invoice saveInvoice(InvoiceCreateDTO dto);
    public List<Invoice> getAllInvoices();
    public Invoice getInvoiceById(Long id);
    public void deleteInvoiceById(Long id);
    public void updateInvoice(Invoice invoice);
}