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
    severity?: DiseaseSeverity;
    description?: string;
    symptoms?: string;
    tips?: string;
}

export class Disease implements IDisease {
    constructor(
        public id?: number,
        public name?: string,
        public severity?: DiseaseSeverity,
        public description?: string,
        public symptoms?: string,
        public tips?: string
    ) {}
}
