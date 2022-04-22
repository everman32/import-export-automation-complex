import { ITransport } from 'app/shared/model/transport.model';
import { IDriver } from 'app/shared/model/driver.model';
import { IAddress } from 'app/shared/model/address.model';
import { IProduct } from 'app/shared/model/product.model';
import { IUser } from 'app/shared/model/user.model';

export interface ITrip {
  id?: number;
  distance?: number;
  transport?: ITransport | null;
  driver?: IDriver | null;
  address?: IAddress | null;
  product?: IProduct | null;
  user?: IUser | null;
}

export const defaultValue: Readonly<ITrip> = {};
