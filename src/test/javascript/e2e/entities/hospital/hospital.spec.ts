/* tslint:disable no-unused-expression */
import { browser, ExpectedConditions as ec, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

import { HospitalComponentsPage, HospitalDeleteDialog, HospitalUpdatePage } from './hospital.page-object';

const expect = chai.expect;

describe('Hospital e2e test', () => {
    let navBarPage: NavBarPage;
    let signInPage: SignInPage;
    let hospitalUpdatePage: HospitalUpdatePage;
    let hospitalComponentsPage: HospitalComponentsPage;
    let hospitalDeleteDialog: HospitalDeleteDialog;

    before(async () => {
        await browser.get('/');
        navBarPage = new NavBarPage();
        signInPage = await navBarPage.getSignInPage();
        await signInPage.autoSignInUsing('admin', 'admin');
        await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
    });

    it('should load Hospitals', async () => {
        await navBarPage.goToEntity('hospital');
        hospitalComponentsPage = new HospitalComponentsPage();
        expect(await hospitalComponentsPage.getTitle()).to.eq('amachouApp.hospital.home.title');
    });

    it('should load create Hospital page', async () => {
        await hospitalComponentsPage.clickOnCreateButton();
        hospitalUpdatePage = new HospitalUpdatePage();
        expect(await hospitalUpdatePage.getPageTitle()).to.eq('amachouApp.hospital.home.createOrEditLabel');
        await hospitalUpdatePage.cancel();
    });

    it('should create and save Hospitals', async () => {
        const nbButtonsBeforeCreate = await hospitalComponentsPage.countDeleteButtons();

        await hospitalComponentsPage.clickOnCreateButton();
        await promise.all([
            hospitalUpdatePage.setNameInput('name'),
            hospitalUpdatePage.setLatInput('5'),
            hospitalUpdatePage.setLonInput('5'),
            hospitalUpdatePage.sizeSelectLastOption()
        ]);
        expect(await hospitalUpdatePage.getNameInput()).to.eq('name');
        expect(await hospitalUpdatePage.getLatInput()).to.eq('5');
        expect(await hospitalUpdatePage.getLonInput()).to.eq('5');
        await hospitalUpdatePage.save();
        expect(await hospitalUpdatePage.getSaveButton().isPresent()).to.be.false;

        expect(await hospitalComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1);
    });

    it('should delete last Hospital', async () => {
        const nbButtonsBeforeDelete = await hospitalComponentsPage.countDeleteButtons();
        await hospitalComponentsPage.clickOnLastDeleteButton();

        hospitalDeleteDialog = new HospitalDeleteDialog();
        expect(await hospitalDeleteDialog.getDialogTitle()).to.eq('amachouApp.hospital.delete.question');
        await hospitalDeleteDialog.clickOnConfirmButton();

        expect(await hospitalComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
    });

    after(async () => {
        await navBarPage.autoSignOut();
    });
});
