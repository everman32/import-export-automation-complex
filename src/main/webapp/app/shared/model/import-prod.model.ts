import { ITrip } from 'app/shared/model/trip.model';
import { IGrade } from 'app/shared/model/grade.model';

export interface IImportProd {
  id?: number;
  arrivalDate?: string;
  trip?: ITrip | null;
  grade?: IGrade | null;
}

export const defaultValue: Readonly<IImportProd> = {};
