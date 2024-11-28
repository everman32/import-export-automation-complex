import { IUser } from 'app/shared/model/user.model';
import { ITransport } from 'app/shared/model/transport.model';
import { IDriver } from 'app/shared/model/driver.model';
import { IPositioning } from 'app/shared/model/positioning.model';

export interface ITrip {
  id?: number;
  authorizedCapital?: number;
  threshold?: number;
  user?: IUser | null;
  transport?: ITransport | null;
  driver?: IDriver | null;
  hubPositioning?: IPositioning | null;
}

export const defaultValue: Readonly<ITrip> = {};
