import { IStatementType } from 'app/shared/model/statement-type.model';
import { IProduct } from 'app/shared/model/product.model';
import { IPositioning } from 'app/shared/model/positioning.model';
import { ITrip } from 'app/shared/model/trip.model';

export interface IStatement {
  id?: number;
  name?: string;
  transportTariff?: number;
  deliveryScope?: number;
  statementType?: IStatementType | null;
  product?: IProduct | null;
  positioning?: IPositioning;
  trip?: ITrip | null;
}

export const defaultValue: Readonly<IStatement> = {};
