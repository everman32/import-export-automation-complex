import { ITrip } from 'app/shared/model/trip.model';

export interface IDriver {
  id?: number;
  firstname?: string;
  patronymic?: string | null;
  lastname?: string;
  phone?: string;
  experience?: number;
  trips?: ITrip[] | null;
}

export const defaultValue: Readonly<IDriver> = {};
