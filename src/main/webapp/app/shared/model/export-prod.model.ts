import dayjs from 'dayjs';
import { ITrip } from 'app/shared/model/trip.model';
import { IGrade } from 'app/shared/model/grade.model';

export interface IExportProd {
  id?: number;
  departuredate?: string;
  trip?: ITrip | null;
  grade?: IGrade | null;
}

export const defaultValue: Readonly<IExportProd> = {};
