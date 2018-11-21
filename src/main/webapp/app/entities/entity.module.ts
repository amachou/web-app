import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { AmachouDiseaseModule } from './disease/disease.module';
import { AmachouHospitalModule } from './hospital/hospital.module';
/* jhipster-needle-add-entity-module-import - JHipster will add entity modules imports here */

@NgModule({
    // prettier-ignore
    imports: [
        AmachouDiseaseModule,
        AmachouHospitalModule,
        /* jhipster-needle-add-entity-module - JHipster will add entity modules here */
    ],
    declarations: [],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AmachouEntityModule {}
