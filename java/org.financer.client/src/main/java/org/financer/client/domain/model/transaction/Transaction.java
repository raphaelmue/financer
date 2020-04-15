package org.financer.client.domain.model.transaction;

import org.financer.client.domain.model.category.Category;
import org.financer.shared.domain.model.AmountProvider;

import java.util.HashSet;
import java.util.Set;

public abstract class Transaction implements AmountProvider {

    private long id;
    private Category category;
    private String description;
    private String vendor;
    private Set<Attachment> attachments = new HashSet<>();

    @Override
    public boolean isFixed() {
        return this.category.isFixed();
    }

    @Override
    public boolean isRevenue() {
        return this.category.isRevenue();
    }


    /*
     * Getters and Setters
     */

    public long getId() {
        return id;
    }

    public Transaction setId(long id) {
        this.id = id;
        return this;
    }

    public Category getCategory() {
        return category;
    }

    public Transaction setCategory(Category category) {

        this.category = category;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Transaction setDescription(String purpose) {
        this.description = purpose;
        return this;
    }

    public String getVendor() {
        return vendor;
    }

    public Transaction setVendor(String vendor) {
        this.vendor = vendor;
        return this;
    }

    public Set<Attachment> getAttachments() {
        return attachments;
    }

    public Transaction setAttachments(Set<Attachment> attachments) {
        this.attachments = attachments;
        return this;
    }

    public Transaction addAttachment(Attachment attachment) {
        this.attachments.add(attachment);
        return this;
    }

    public Transaction removeAttachment(Attachment attachment) {
        this.attachments.remove(attachment);
        return this;
    }

    @Override
    public String toString() {
        return "Transaction[" + "id=" + id + ", category=" + category + ", vendor='" + vendor + '\'' + ']';
    }
}
