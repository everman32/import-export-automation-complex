import { IProduct } from 'app/shared/model/product.model';

export interface IProductUnit {
  id?: number;
  name?: string;
  description?: string;
  products?: IProduct[] | null;
}

export const defaultValue: Readonly<IProductUnit> = {};
