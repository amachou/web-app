/* tslint:disable no-unused-expression */
import { browser, ExpectedConditions as ec, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

import { DiseaseComponentsPage, DiseaseDeleteDialog, DiseaseUpdatePage } from './disease.page-object';

const expect = chai.expect;

describe('Disease e2e test', () => {
    let navBarPage: NavBarPage;
    let signInPage: SignInPage;
    let diseaseUpdatePage: DiseaseUpdatePage;
    let diseaseComponentsPage: DiseaseComponentsPage;
    let diseaseDeleteDialog: DiseaseDeleteDialog;

    before(async () => {
        await browser.get('/');
        navBarPage = new NavBarPage();
        signInPage = await navBarPage.getSignInPage();
        await signInPage.autoSignInUsing('admin', 'admin');
        await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
    });

    it('should load Diseases', async () => {
        await navBarPage.goToEntity('disease');
        diseaseComponentsPage = new DiseaseComponentsPage();
        expect(await diseaseComponentsPage.getTitle()).to.eq('amachouApp.disease.home.title');
    });

    it('should load create Disease page', async () => {
        await diseaseComponentsPage.clickOnCreateButton();
        diseaseUpdatePage = new DiseaseUpdatePage();
        expect(await diseaseUpdatePage.getPageTitle()).to.eq('amachouApp.disease.home.createOrEditLabel');
        await diseaseUpdatePage.cancel();
    });

    it('should create and save Diseases', async () => {
        const nbButtonsBeforeCreate = await diseaseComponentsPage.countDeleteButtons();

        await diseaseComponentsPage.clickOnCreateButton();
        await promise.all([
            diseaseUpdatePage.setNameInput('name'),
            diseaseUpdatePage.severitySelectLastOption(),
            diseaseUpdatePage.setDescriptionInput('description'),
            diseaseUpdatePage.setSymptomsInput('symptoms'),
            diseaseUpdatePage.setTipsInput('tips')
        ]);
        expect(await diseaseUpdatePage.getNameInput()).to.eq('name');
        expect(await diseaseUpdatePage.getDescriptionInput()).to.eq('description');
        expect(await diseaseUpdatePage.getSymptomsInput()).to.eq('symptoms');
        expect(await diseaseUpdatePage.getTipsInput()).to.eq('tips');
        await diseaseUpdatePage.save();
        expect(await diseaseUpdatePage.getSaveButton().isPresent()).to.be.false;

        expect(await diseaseComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1);
    });

    it('should delete last Disease', async () => {
        const nbButtonsBeforeDelete = await diseaseComponentsPage.countDeleteButtons();
        await diseaseComponentsPage.clickOnLastDeleteButton();

        diseaseDeleteDialog = new DiseaseDeleteDialog();
        expect(await diseaseDeleteDialog.getDialogTitle()).to.eq('amachouApp.disease.delete.question');
        await diseaseDeleteDialog.clickOnConfirmButton();

        expect(await diseaseComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
    });

    after(async () => {
        await navBarPage.autoSignOut();
    });
});
