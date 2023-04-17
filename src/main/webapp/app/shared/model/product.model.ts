import { IStatement } from 'app/shared/model/statement.model';
import { IProductUnit } from 'app/shared/model/product-unit.model';

export interface IProduct {
  id?: number;
  name?: string;
  costPerPiece?: number;
  statements?: IStatement[] | null;
  productUnit?: IProductUnit | null;
}

export const defaultValue: Readonly<IProduct> = {};
