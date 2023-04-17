import { IStatement } from 'app/shared/model/statement.model';
import { ITrip } from 'app/shared/model/trip.model';

export interface IPositioning {
  id?: number;
  latitude?: number;
  longitude?: number;
  statements?: IStatement[] | null;
  trips?: ITrip[] | null;
}

export const defaultValue: Readonly<IPositioning> = {};
