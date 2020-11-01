import {Amount}   from '../../../../.openapi/models';
import {BaseType} from 'antd/lib/typography/Base';

const positiveColor: string = '#3f8600';
const negativeColor: string = '#cf1322';

export default class AmountUtil {

    public static getColor(amount: Amount): string {
        return amount.amount < 0 ? negativeColor : positiveColor;
    }

    public static getTextType(amount: Amount): BaseType {
        return amount.amount < 0 ? 'danger' : 'success';
    }

}
