export const enum HospitalSize {
    SMALL = 'SMALL',
    MEDIUM = 'MEDIUM',
    BIG = 'BIG'
}

export interface IHospital {
    id?: number;
    name?: string;
    lat?: number;
    lon?: number;
    size?: HospitalSize;
}

export class Hospital implements IHospital {
    constructor(public id?: number, public name?: string, public lat?: number, public lon?: number, public size?: HospitalSize) {}
}
