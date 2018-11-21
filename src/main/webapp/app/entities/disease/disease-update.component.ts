import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { IDisease } from 'app/shared/model/disease.model';
import { DiseaseService } from './disease.service';

@Component({
    selector: 'jhi-disease-update',
    templateUrl: './disease-update.component.html'
})
export class DiseaseUpdateComponent implements OnInit {
    disease: IDisease;
    isSaving: boolean;

    constructor(private diseaseService: DiseaseService, private activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ disease }) => {
            this.disease = disease;
        });
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        if (this.disease.id !== undefined) {
            this.subscribeToSaveResponse(this.diseaseService.update(this.disease));
        } else {
            this.subscribeToSaveResponse(this.diseaseService.create(this.disease));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<IDisease>>) {
        result.subscribe((res: HttpResponse<IDisease>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    private onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    private onSaveError() {
        this.isSaving = false;
    }
}
