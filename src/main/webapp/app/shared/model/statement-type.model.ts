import { IStatement } from 'app/shared/model/statement.model';

export interface IStatementType {
  id?: number;
  name?: string;
  statements?: IStatement[] | null;
}

export const defaultValue: Readonly<IStatementType> = {};
