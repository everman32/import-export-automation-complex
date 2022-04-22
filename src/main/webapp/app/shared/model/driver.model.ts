export interface IDriver {
  id?: number;
  firstname?: string;
  patronymic?: string | null;
  lastname?: string;
  phone?: string;
  experience?: number;
}

export const defaultValue: Readonly<IDriver> = {};
