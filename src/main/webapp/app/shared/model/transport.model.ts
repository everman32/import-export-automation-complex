import { ITrip } from 'app/shared/model/trip.model';

export interface ITransport {
  id?: number;
  brand?: string;
  model?: string;
  vin?: string;
  trips?: ITrip[] | null;
}

export const defaultValue: Readonly<ITransport> = {};
