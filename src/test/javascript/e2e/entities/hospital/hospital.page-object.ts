import { element, by, ElementFinder } from 'protractor';

export class HospitalComponentsPage {
    createButton = element(by.id('jh-create-entity'));
    deleteButtons = element.all(by.css('jhi-hospital div table .btn-danger'));
    title = element.all(by.css('jhi-hospital div h2#page-heading span')).first();

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

export class HospitalUpdatePage {
    pageTitle = element(by.id('jhi-hospital-heading'));
    saveButton = element(by.id('save-entity'));
    cancelButton = element(by.id('cancel-save'));
    nameInput = element(by.id('field_name'));
    latInput = element(by.id('field_lat'));
    lonInput = element(by.id('field_lon'));
    sizeSelect = element(by.id('field_size'));

    async getPageTitle() {
        return this.pageTitle.getAttribute('jhiTranslate');
    }

    async setNameInput(name) {
        await this.nameInput.sendKeys(name);
    }

    async getNameInput() {
        return this.nameInput.getAttribute('value');
    }

    async setLatInput(lat) {
        await this.latInput.sendKeys(lat);
    }

    async getLatInput() {
        return this.latInput.getAttribute('value');
    }

    async setLonInput(lon) {
        await this.lonInput.sendKeys(lon);
    }

    async getLonInput() {
        return this.lonInput.getAttribute('value');
    }

    async setSizeSelect(size) {
        await this.sizeSelect.sendKeys(size);
    }

    async getSizeSelect() {
        return this.sizeSelect.element(by.css('option:checked')).getText();
    }

    async sizeSelectLastOption() {
        await this.sizeSelect
            .all(by.tagName('option'))
            .last()
            .click();
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

export class HospitalDeleteDialog {
    private dialogTitle = element(by.id('jhi-delete-hospital-heading'));
    private confirmButton = element(by.id('jhi-confirm-delete-hospital'));

    async getDialogTitle() {
        return this.dialogTitle.getAttribute('jhiTranslate');
    }

    async clickOnConfirmButton() {
        await this.confirmButton.click();
    }
}
