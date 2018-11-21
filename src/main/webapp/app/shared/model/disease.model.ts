export const enum DiseaseSeverity {
    LOW = 'LOW',
    MEDIUM = 'MEDIUM',
    HIGH = 'HIGH',
    SEVERE = 'SEVERE',
    DEADLY = 'DEADLY'
}

export interface IDisease {
    id?: number;
    name?: string;
    description?: string;
    severity?: DiseaseSeverity;
    symptoms?: string;
    tips?: string;
}

export class Disease implements IDisease {
    constructor(
        public id?: number,
        public name?: string,
        public description?: string,
        public severity?: DiseaseSeverity,
        public symptoms?: string,
        public tips?: string
    ) {}
}
