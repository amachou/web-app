import { element, by, ElementFinder } from 'protractor';

export class DiseaseComponentsPage {
    createButton = element(by.id('jh-create-entity'));
    deleteButtons = element.all(by.css('jhi-disease div table .btn-danger'));
    title = element.all(by.css('jhi-disease div h2#page-heading span')).first();

    async clickOnCreateButton() {
        await this.createButton.click();
    }

    async clickOnLastDeleteButton() {
        await this.deleteButtons.last().click();
    }

    async countDeleteButtons() {
        return this.deleteButtons.count();
    }

    async getTitle() {
        return this.title.getAttribute('jhiTranslate');
    }
}

export class DiseaseUpdatePage {
    pageTitle = element(by.id('jhi-disease-heading'));
    saveButton = element(by.id('save-entity'));
    cancelButton = element(by.id('cancel-save'));
    nameInput = element(by.id('field_name'));
    descriptionInput = element(by.id('field_description'));
    severitySelect = element(by.id('field_severity'));
    symptomsInput = element(by.id('field_symptoms'));
    tipsInput = element(by.id('field_tips'));

    async getPageTitle() {
        return this.pageTitle.getAttribute('jhiTranslate');
    }

    async setNameInput(name) {
        await this.nameInput.sendKeys(name);
    }

    async getNameInput() {
        return this.nameInput.getAttribute('value');
    }

    async setDescriptionInput(description) {
        await this.descriptionInput.sendKeys(description);
    }

    async getDescriptionInput() {
        return this.descriptionInput.getAttribute('value');
    }

    async setSeveritySelect(severity) {
        await this.severitySelect.sendKeys(severity);
    }

    async getSeveritySelect() {
        return this.severitySelect.element(by.css('option:checked')).getText();
    }

    async severitySelectLastOption() {
        await this.severitySelect
            .all(by.tagName('option'))
            .last()
            .click();
    }

    async setSymptomsInput(symptoms) {
        await this.symptomsInput.sendKeys(symptoms);
    }

    async getSymptomsInput() {
        return this.symptomsInput.getAttribute('value');
    }

    async setTipsInput(tips) {
        await this.tipsInput.sendKeys(tips);
    }

    async getTipsInput() {
        return this.tipsInput.getAttribute('value');
    }

    async save() {
        await this.saveButton.click();
    }

    async cancel() {
        await this.cancelButton.click();
    }

    getSaveButton(): ElementFinder {
        return this.saveButton;
    }
}

export class DiseaseDeleteDialog {
    private dialogTitle = element(by.id('jhi-delete-disease-heading'));
    private confirmButton = element(by.id('jhi-confirm-delete-disease'));

    async getDialogTitle() {
        return this.dialogTitle.getAttribute('jhiTranslate');
    }

    async clickOnConfirmButton() {
        await this.confirmButton.click();
    }
}
